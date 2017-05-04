package com.mindplus.vote;

import java.util.HashMap;

public class VoteController {
	private HashMap<String, Vote> roomVoteMap = null;
	private HashMap<String, Vote> idVoteMap = null;

	private static VoteController _instance = null;

	private VoteController() {
		this.roomVoteMap = new HashMap<String, Vote>();
		this.idVoteMap = new HashMap<String, Vote>();
	}

	public static VoteController getInstance() {
		if (_instance == null) {
			_instance = new VoteController();
		}
		return _instance;
	}

	public void startRoomVote(String roomId, int memberTotal) {
		roomVoteMap.put(roomId, new Vote(memberTotal));
	}

	public boolean voteRoom(String roomId, boolean value) {
		Vote vote = roomVoteMap.get(roomId);
		vote.vote(value);
		return vote.isAllVoted();
	}

	public boolean getRoomResult(String roomId) {
		boolean ret = false;
		Vote vote = roomVoteMap.remove(roomId);

		if (vote != null) {
			ret = vote.getResult();
		}
		return ret;
	}

	public void startIdVote(String id, int memberTotal) {
		idVoteMap.put(id, new Vote(memberTotal));
	}

	public boolean voteId(String id, boolean value) {
		Vote vote = idVoteMap.get(id);
		vote.vote(value);
		return vote.isAllVoted();
	}

	public boolean getIdResult(String id) {
		boolean ret = false;
		Vote vote = idVoteMap.remove(id);

		if (vote != null) {
			ret = vote.getResult();
		}
		return ret;
	}
}
