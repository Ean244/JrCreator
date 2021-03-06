package com.github.ean244.JrCreator.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;

import net.dv8tion.jda.core.entities.Member;

public class TrackScheduler {
	private final List<TrackWrapper> playlist;
	private final Map<Member, AudioPlaylist> memberSelectedTrack;
	
	public TrackScheduler() {
		this.playlist = new ArrayList<>();
		this.memberSelectedTrack = new HashMap<>();
	}
	
	public TrackWrapper loadSelectedTrack(Member member, int index) {
		TrackWrapper wrapper = new TrackWrapper(memberSelectedTrack.get(member).getTracks().get(index), member);
		loadTrack(wrapper);
		memberSelectedTrack.remove(member);
		return wrapper;
	}
	
	public void assignTrack(Member member, AudioPlaylist list) {
		memberSelectedTrack.put(member, list);
	}
	
	public boolean hasSelected(Member member) {
		return memberSelectedTrack.containsKey(member);
	}
	
	public TrackWrapper currentTrack() {
		if(playlist.isEmpty()) 
			throw new IllegalArgumentException("No tracks in playlist");
		
		return playlist.get(0);
	}
	
	public void loadTrack(TrackWrapper track) {
		if(track == null)
			throw new IllegalStateException("Track cannot be null");
		
		this.playlist.add(track);
	}
	
	public void next() {
		playlist.remove(0);
	}
	
	public boolean hasNext() {
		// 1 for current song
		return playlist.size() > 1;
	}
	
	public boolean isPlaylistEmpty() {
		return playlist.isEmpty();
	}
	
	public void clearPlaylistSongs() {
		playlist.clear();
	}
	
	public List<TrackWrapper> getPlaylist() {
		return Collections.unmodifiableList(playlist);
	}
}
