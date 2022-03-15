package com.jumia.phonenumbersapi.IT;

import com.jumia.phonenumbersapi.IT.TestRestTemplate;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Durations.TWO_MINUTES;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class ITBaseContextExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            started = true;
            Awaitility.setDefaultTimeout(TWO_MINUTES);
            with().pollInterval(fibonacci().with().unit(SECONDS).and().offset(4)).await().until(TestRestTemplate::healthCheckCall);
            with().pollInterval(fibonacci().with().unit(SECONDS).and().offset(4)).await().until(TestRestTemplate::testCall);
            context.getRoot().getStore(GLOBAL).put("CALL_BACK_HOOK_TO_THIS_CONTEXT_EXTENSION_TO_CALL_CLOSE", this);
        }
    }

    @Override
    public void close() {
    }
}

