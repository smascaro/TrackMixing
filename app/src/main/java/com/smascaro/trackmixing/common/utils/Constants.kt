package com.smascaro.trackmixing.common.utils

//TODO: move to resource file
const val NODE_SERVER_NAME = "DESKTOP-L9MQBB7"

//TODO: move to resource file
const val NODE_SERVER_PORT = 8081
const val NODE_BASE_URL = "http://$NODE_SERVER_NAME:$NODE_SERVER_PORT/"
const val NODE_API_FETCH_AVAILABLE = "availableTracks"
const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
const val PLAYER_NOTIFICATION_ID = 2000
const val PLAYER_NOTIFICATION_MEDIA_SESSION_TAG = "PLAYER_NOTIFICATION_MEDIA_SESSION_TAG"
const val PLAYER_NOTIFICATION_ACTION_PLAY_MASTER = "PLAYER_NOTIFICATION_ACTION_PLAY_MASTER"
const val PLAYER_NOTIFICATION_ACTION_PAUSE_MASTER = "PLAYER_NOTIFICATION_ACTION_PAUSE_MASTER"
const val PLAYER_NOTIFICATION_ACTION_MUTE_VOCALS = "PLAYER_NOTIFICATION_ACTION_MUTE_VOCALS"
const val PLAYER_NOTIFICATION_ACTION_UNMUTE_VOCALS = "PLAYER_NOTIFICATION_ACTION_UNMUTE_VOCALS"
const val PLAYER_NOTIFICATION_ACTION_MUTE_OTHER = "PLAYER_NOTIFICATION_ACTION_MUTE_OTHER"
const val PLAYER_NOTIFICATION_ACTION_UNMUTE_OTHER = "PLAYER_NOTIFICATION_ACTION_UNMUTE_OTHER"
const val PLAYER_NOTIFICATION_ACTION_MUTE_BASS = "PLAYER_NOTIFICATION_ACTION_MUTE_BASS"
const val PLAYER_NOTIFICATION_ACTION_UNMUTE_BASS = "PLAYER_NOTIFICATION_ACTION_UNMUTE_BASS"
const val PLAYER_NOTIFICATION_ACTION_MUTE_DRUMS = "PLAYER_NOTIFICATION_ACTION_MUTE_DRUMS"
const val PLAYER_NOTIFICATION_ACTION_UNMUTE_DRUMS = "PLAYER_NOTIFICATION_ACTION_UNMUTE_DRUMS"
const val PLAYER_NOTIFICATION_ACTION_START_SERVICE = "PLAYER_NOTIFICATION_ACTION_START_SERVICE"
const val PLAYER_NOTIFICATION_ACTION_STOP_SERVICE = "PLAYER_NOTIFICATION_ACTION_STOP_SERVICE"
const val PLAYER_NOTIFICATION_ACTION_LOAD_TRACK = "PLAYER_NOTIFICATION_ACTION_LOAD_TRACK"
const val PLAYER_NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY =
    "PLAYER_NOTIFICATION_EXTRA_LOAD_TRACK_PARAM_KEY"

const val DOWNLOAD_NOTIFICATION_ID = 2001
const val DOWNLOAD_NOTIFICATION_ACTION_START_DOWNLOAD = "DOWNLOAD_NOTIFICATION_ACTION_START_SERVICE"
const val DOWNLOAD_NOTIFICATION_EXTRA_START_SERVICE_PARAM_KEY =
    "DOWNLOAD_NOTIFICATION_EXTRA_START_SERVICE_PARAM_KEY"

//Shared preferences
const val SHARED_PREFERENCES_PLAYBACK = "SHARED_PREFERENCES_PLAYBACK"
const val SHARED_PREFERENCES_PLAYBACK_IS_PLAYING = "SHARED_PREFERENCES_PLAYBACK_IS_PLAYING"
const val SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING = "SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING"