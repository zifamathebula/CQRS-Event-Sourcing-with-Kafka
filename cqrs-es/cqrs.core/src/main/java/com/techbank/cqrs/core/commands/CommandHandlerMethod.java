package com.techbank.cqrs.core.commands;

@FunctionalInterface
public interface CommandHandlerMethod<T extends BaseCommand> {
    Response handle(T command)
}
