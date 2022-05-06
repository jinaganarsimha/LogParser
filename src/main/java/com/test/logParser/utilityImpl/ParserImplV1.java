package com.test.logParser.utilityImpl;

import com.test.logParser.LogParserApplication;
import com.test.logParser.entities.Event;
import com.test.logParser.entities.EventType;
import com.test.logParser.repository.EventRepository;
import com.test.logParser.utility.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Implementation class to read events and save to db
 */
@Component
public class ParserImplV1 implements Parser {
    private EventRepository eventRepository;

    private final Logger logger = LogManager.getLogger(ParserImplV1.class);

    @Value("${log.filename}")
    private String fileName;

    @Value("${db.inmemory}")
    private boolean inMemoryDB;

    //Frequently used strings
    private final String ID = "id";
    private final String TIMESTAMP = "timestamp";
    private final String HOST = "host";
    private final String TYPE = "type";
    @Autowired
    ParserImplV1(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    /**
     * Stores or updates the events in db
     * @param eventAsString  Event details in string form
     * @return  Final event object
     */
    public Event persistEvent(String eventAsString){
        JSONObject eventAsJson = new JSONObject(eventAsString);

        Optional<Event> eventFromDB = eventRepository.findById(eventAsJson.getString(ID));
        if(eventFromDB.isPresent()){
            if(eventAsJson.has(TIMESTAMP)){
                Long eventDuration= Math.abs(eventFromDB.get().getTimestamp()-eventAsJson.getLong(TIMESTAMP));
                eventFromDB.get().setDuration(eventDuration);
                if(eventDuration > 4) eventFromDB.get().setAlert(true);
            }
            if(eventAsJson.has(HOST)){
                eventFromDB.get().setHost(eventAsJson.getString(HOST));
            }
            logger.info(String.format("Updating the event in db with id: %s", eventFromDB.get().getId()));
            return eventRepository.save(eventFromDB.get());
        }
        else{
            Event newEvent = new Event();
            if(eventAsJson.has(ID)){
                newEvent.setId(eventAsJson.getString(ID));
            }
            if(eventAsJson.has(TYPE)){
                newEvent.setType( eventAsJson.getString(TYPE).equals("STARTED") ? EventType.STARTED : EventType.FINISHED);
            }
            if(eventAsJson.has(TIMESTAMP)){
                newEvent.setTimestamp(eventAsJson.getLong(TIMESTAMP));
            }
            if(eventAsJson.has(HOST)){
                newEvent.setHost(eventAsJson.getString(HOST));
            }
            newEvent.setAlert(false);
            logger.info(String.format("Storing the event in db with id: %s", newEvent.getId()));
            return eventRepository.save(newEvent);
        }
    }

    /**
     * Method to process each line from log file
     * @param currentEvent  Current string under process
     * @return  remaining un-processed string in case of >1 events per line
     */
    public StringBuffer processLineFromLogFile(StringBuffer currentEvent){
        int startingIndex= currentEvent.indexOf("{"), endingIndex= currentEvent.indexOf("}");
        if(startingIndex!=-1 && endingIndex!=-1) {
            do{
                persistEvent(currentEvent.substring(startingIndex, endingIndex+1));
                currentEvent = new StringBuffer(currentEvent.substring(endingIndex+1));
                startingIndex= currentEvent.indexOf("{"); endingIndex= currentEvent.indexOf("}");
            }while(startingIndex!=-1 && endingIndex!=-1);
        }
        return currentEvent;
    }

    /**
     * Method to read each line of log file
     * @param filePath  Path of file to be read
     * @throws IOException
     */
    public void readFile(String filePath) throws IOException {
        logger.info("Started reading file..");
        FileInputStream inputStream = null;
        Scanner sc = null;
        StringBuffer currentEvent= new StringBuffer("");
        try {
            inputStream = new FileInputStream(filePath+fileName);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                logger.debug(String.format("Processing line... %s", line));
                currentEvent= processLineFromLogFile(currentEvent.append(line));
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
        logger.info("Ended reading file..");

        //Printing the entire table as i'm using in memory db
        //It can be disable from applicaion.properties
        if(inMemoryDB){
            logger.info("Printing entire events table...");
            List<Event> events = eventRepository.findAll();

            for(Event event : events){
                System.out.println("Id: "+ event.getId());
                System.out.println("duration: "+ event.getDuration());
                System.out.println("type: "+ event.getType());
                System.out.println("timestamp: "+ event.getTimestamp());
                System.out.println("host: "+ event.getHost());
                System.out.println("alert: "+ event.isAlert()+"\n");
            }
        }
    }
}
