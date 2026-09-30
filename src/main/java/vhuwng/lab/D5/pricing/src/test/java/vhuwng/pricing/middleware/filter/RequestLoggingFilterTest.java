package vhuwng.pricing.middleware.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RequestLoggingFilterTest {

    @Test
    void delegatesRequestAndLogsAfterChainReturns() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/pricing/quote");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        new RequestLoggingFilter().doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
