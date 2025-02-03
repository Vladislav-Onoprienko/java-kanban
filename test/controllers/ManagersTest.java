package controllers;

import main.controllers.InMemoryTaskManager;
import main.controllers.Managers;
import main.controllers.InMemoryHistoryManager;
import main.controllers.TaskManager;
import main.controllers.HistoryManager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    // Проверяем, что Managers всегда возвращает проинициализированный и готовый к работе InMemoryTaskManager
    @Test
    public void shouldGetInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();

        // Проверяем, что возвращаемый менеджер задач не null
        assertNotNull(manager, "Менеджер задач должен быть проинициализирован.");

        assertInstanceOf(InMemoryTaskManager.class, manager, "Менеджер задач должен быть экземпляром InMemoryTaskManager.");
    }

    // Проверяем, что Managers всегда возвращает проинициализированный и готовый к работе InMemoryHistoryManager
    @Test
    public void shouldGetInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        // Проверяем, что возвращаемый менеджер истории не null
        assertNotNull(historyManager, "Менеджер истории должен быть проинициализирован.");

        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "Менеджер истории должен быть экземпляром InMemoryHistoryManager.");
    }

}