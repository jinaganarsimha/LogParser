package com.test.logParser.utilityImpl;

import com.test.logParser.entities.Event;
import com.test.logParser.entities.EventType;
import com.test.logParser.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class ParserImplV1Test {

    //@Autowired
    @MockBean
    private EventRepository eventRepository;

    @Autowired
    private ParserImplV1 parserImplV1;
    private final Logger logger = LogManager.getLogger(ParserImplV1Test.class);

    @Before
    public void init() {
        logger.info("Test startup...");
        parserImplV1 = new ParserImplV1(eventRepository);
    }
    @Test
    void whenStoringFinishedEvent_thenDurationGetsUpdated() {
        Event eventInDB = new Event("test", 0, EventType.STARTED, 123123, "test");
        Event expectedEvent = new Event("test", 1, EventType.STARTED, 123123, "test");
        when(eventRepository.save(Mockito.any())).thenReturn(expectedEvent);
        when(eventRepository.getById("test")).thenReturn(eventInDB);
        Event actual = parserImplV1.persistEvent("{\"id\":\"test\", \"type\": \"FINISHED\",\"host\": \"test\", \"timestamp\": 123124}");
        assertEquals(expectedEvent.getDuration(), actual.getDuration());
    }
}