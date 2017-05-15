# Multi-Server Chat System

Multi-Server Chat System - Server end:

com.mindplus.clock:

MessageBuffer.java - A priority queue for messages that are recevied before their previous message.

VCController.java - Controller of Vector Clock.

VectorClock.java - Implementation of Vector Clock.

com.mindplus.command:

ClientCmdHandler.java - Command handler for commands from client end.

ClientServerCmd.java - Implementation of Command between Client and Server end.

CmdHandler.java - Abstract class of command handler.

CmdHandlerInf.java - Interface of command handler.

Command.java - Super class of all command.

CoordinateCmdHandler.java - Command Handler for commands from other servers.

InternelCmd.java - Internal command between threads.

erverBeaterHandler.java - Heart beat handler to detect failure.

ServerServerCmd.java - Implement of command among servers.

com.mindplus.configuration:

Configuration.java - Implementation of configurate file.

ConfigurationHandler.java - Convert xml to Java object.

RouterConfig.java - Setting of router module.

ServerConfig.java - Setting of server module.

com.mindplus.heartbeat:

BeatAccepter.java - Accepter for new heart beatter.

BeaterList.java - List of current beater being listened.

Callback.java - Abstract class of call back after heart beat is recevied.

TimeStampAndCmd.java - Time stamp and Command received formatter.

Watchdog.java - Watch dog of heart beat.

com.mindplus.listener:

ClientListener.java - Listener for new client.

CoordinateListener.java - Listener for new server.

MsgLister.java - Listener of messages.

com.mindplus.mian:

Main.java - Entry of the application.

ServerArguments.java - Argument parser of command line.

com.mindplus.messge:

Message.java - Implementation of message in all communications.

com.mindplus.messagequeue:

MessageQueue.java - Message Queue of messages received in application layer.

com.mindplus.model:

ChatRoom.java - Implementation of chat room.

ChatRoomListController - Controller of chat room list.

Client.java -Implementation of client.

ClientListController - Controller of client list.

ServerListController - Controller of server list.

com.mindplus.security:

ServerVerification.java - Verification of new server.

UserVerification.java - Verification of new user.

Verification.java - Super class of verification.

com.mindplus.snapshot:

Recovery.java - Implementation of recovery from snapshot.

SnapShot.java - Implementation of snapshot.

SnapShotController.java - Controller of snap shot.

com.mindplus.userdata:

User.java - Implementation of User.

UserData.java - Model of user data.

UserDataController.java - Convertor from xml to Java object.

com.mindplus.vote:

Vote.java - Vote for agreement of result after broadcast.

VoteController.java - Controller of vote.
