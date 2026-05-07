package com.github.swim_developer.validator.ffice.provider;

import com.github.swim_developer.validator.ffice.provider.application.usecase.FficeMessageExtractor;
import com.github.swim_developer.validator.ffice.provider.domain.model.FficeMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FficeMessageExtractorTest {

    private final FficeMessageExtractor extractor = new FficeMessageExtractor();

    @Test
    void extractsFiledFlightPlanType() {
        String xml = "<ffice:FficeMessage><FiledFlightPlan><gufi>LFPO-LPPT-20260507-001</gufi></FiledFlightPlan></ffice:FficeMessage>";
        FficeMessage result = extractor.extract(xml);
        assertThat(result.messageType()).isEqualTo("FILED_FLIGHT_PLAN");
    }

    @Test
    void extractsFlightDepartureType() {
        String xml = "<ffice:FficeMessage><FlightDeparture><gufi>EHAM-LPPT-20260507-002</gufi></FlightDeparture></ffice:FficeMessage>";
        FficeMessage result = extractor.extract(xml);
        assertThat(result.messageType()).isEqualTo("FLIGHT_DEPARTURE");
    }

    @Test
    void extractsFlightArrivalType() {
        String xml = "<ffice:FficeMessage><FlightArrival><gufi>EGLL-LPPT-20260507-003</gufi></FlightArrival></ffice:FficeMessage>";
        FficeMessage result = extractor.extract(xml);
        assertThat(result.messageType()).isEqualTo("FLIGHT_ARRIVAL");
    }

    @Test
    void extractsFlightCancellationType() {
        String xml = "<ffice:FficeMessage><FlightCancellation/></ffice:FficeMessage>";
        FficeMessage result = extractor.extract(xml);
        assertThat(result.messageType()).isEqualTo("FLIGHT_CANCELLATION");
    }

    @Test
    void extractsGufi() {
        String xml = "<ffice:FficeMessage><FiledFlightPlan><gufi>LFPO-LPPT-20260507-001</gufi></FiledFlightPlan></ffice:FficeMessage>";
        FficeMessage result = extractor.extract(xml);
        assertThat(result.gufi()).isEqualTo("LFPO-LPPT-20260507-001");
    }

    @Test
    void handlesNullBody() {
        FficeMessage result = extractor.extract(null);
        assertThat(result.messageType()).isEqualTo("UNKNOWN");
        assertThat(result.gufi()).isEqualTo("UNKNOWN");
    }

    @Test
    void handlesBlankBody() {
        FficeMessage result = extractor.extract("   ");
        assertThat(result.messageType()).isEqualTo("UNKNOWN");
    }
}
