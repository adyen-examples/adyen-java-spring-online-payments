package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    private static final String ANY = "ANY";
    private MockMvc mockMvc;

    private CheckoutController sut;

    @Mock
    private ApplicationProperty applicationProperty;

    static class StandaloneMvcTestViewResolver extends InternalResourceViewResolver {

        public StandaloneMvcTestViewResolver() {
            super();
        }

        @Override
        protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
            final InternalResourceView view = (InternalResourceView) super.buildView(viewName);
            // prevent checking for circular view paths
            view.setPreventDispatchLoop(false);
            return view;
        }
    }

    @BeforeEach
    public void setUpBeforeEach() {
        when(applicationProperty.getClientKey()).thenReturn("testClientKey");
        sut = new CheckoutController(applicationProperty);
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
            .setViewResolvers(new StandaloneMvcTestViewResolver())
            .build();
    }

    @Test
    void testCheckOut() throws Exception {

        when(applicationProperty.getClientKey()).thenReturn(ANY);

        mockMvc.perform(get("/checkout")
                .param("type", ANY))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void testGetResult() throws Exception {
        mockMvc.perform(get("/result/{type}", ANY))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void testRedirect() throws Exception {
        mockMvc.perform(get("/redirect"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void testPreview() throws Exception {
        mockMvc.perform(get("/preview")
                .param("type", ANY))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk());
    }
}
