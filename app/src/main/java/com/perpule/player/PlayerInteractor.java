package com.perpule.player;

import com.perpule.data.Song;

/**
 * Created by mani on 16/12/17.
 */

public interface PlayerInteractor {
  void prepare();
  void play(Song song);
  void pause();
  void release();
  void seek();
}
