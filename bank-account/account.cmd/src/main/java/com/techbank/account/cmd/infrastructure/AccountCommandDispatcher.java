package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.core.commands.BaseCommand;
import com.techbank.cqrs.core.commands.CommandHandlerMethod;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by Zifa Mathebula
 */
@Service
public class AccountCommandDispatcher implements CommandDispatcher {

    private final Map<Class<? extends BaseCommand>, List<CommandHandlerMethod>> routes = new HashMap<>();

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(handler);

        var handlers = routes.computeIfAbsent(type, c -> new LinkedList<>());
        handlers.add(handler);
    }

    @Override
    public void send(BaseCommand command) {
        Objects.requireNonNull(command);

        var handlers = routes.get(command.getClass());
        if (handlers == null | handlers.isEmpty()) {
            throw new RuntimeException("No command handler was registered");
        }
        if (handlers.size() > 1) {
            throw new RuntimeException("Cannot send command to more than one handler");
        }

        handlers.stream().findFirst().ifPresent(handler -> handler.handle(command));
    }
}
