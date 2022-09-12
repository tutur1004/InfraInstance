package fr.milekat.infra.messaging;

public enum MessagingCase {
    //  From Proxy To Host
    INVITE_SENT,
    INVITE_RESULT_NOT_FOUND,
    INVITE_RESULT_DENY,

    //  From Host To Proxy
    GAME_READY,
    GAME_FINISHED,
    HOST_JOINED,

    HOST_INVITE_PLAYER,
    HOST_DENIED_REQUEST,

    //  From Lobby To Host
    JOIN_REQUEST,               // TODO

    //  From Lobby To Proxy
    ASK_CREATE_HOST,            // TODO
}
