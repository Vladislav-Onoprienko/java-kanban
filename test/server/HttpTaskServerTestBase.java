package server;
import com.google.gson.Gson;
import main.controllers.InMemoryTaskManager;
import main.controllers.TaskManager;
import main.server.BaseHttpHandler;
import main.server.HttpTaskServer;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.http.HttpClient;

public abstract class HttpTaskServerTestBase {
    protected TaskManager manager;
    protected HttpTaskServer taskServer;
    protected Gson gson;
    protected HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        gson = BaseHttpHandler.getGson();
        taskServer = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }
}
