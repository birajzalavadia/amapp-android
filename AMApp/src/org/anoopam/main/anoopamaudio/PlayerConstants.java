package org.anoopam.main.anoopamaudio;

import android.content.ContentValues;
import android.os.Handler;

import java.util.ArrayList;

public class PlayerConstants {
	//List of Songs
	public static ArrayList<ContentValues> SONGS_LIST = new ArrayList<ContentValues>();
	//song number which is playing right now from SONGS_LIST
	public static int SONG_NUMBER = -1;
	//song is playing or paused
	public static boolean SONG_PAUSED = true;
	//song changed (next, previous)
	public static boolean SONG_CHANGED = false;
	//handler for song changed(next, previous) defined in service(SongService)
	public static Handler SONG_CHANGE_HANDLER;
	//handler for song play/pause defined in service(SongService)
	public static Handler PLAY_PAUSE_HANDLER;
	//handler for showing song progress defined in Activities(MainActivity, AudioPlayerActivity)
	public static Handler PROGRESSBAR_HANDLER;

	public static ContentValues CATEGORY;

}
