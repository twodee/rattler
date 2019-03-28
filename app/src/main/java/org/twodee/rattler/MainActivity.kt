package org.twodee.rattler

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import java.io.File
import java.lang.NumberFormatException

class MainActivity : PermittedActivity() {
  private lateinit var notesBox: EditText
  private lateinit var titleBox: EditText
  private lateinit var beatsPerMinuteBox: EditText
  private lateinit var prefs: SharedPreferences

  private var player: MediaPlayer? = null
  private var song = Song("", 100, "4c4 4c4 4g4 4g4 4a4 4a4 2g4")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    prefs = PreferenceManager.getDefaultSharedPreferences(this)

    song.title = prefs.getString("title", song.title)!!
    song.beatsPerMinute = prefs.getInt("beatsPerMinute", song.beatsPerMinute)
    song.notes = prefs.getString("notes", song.notes)!!

    titleBox = findViewById(R.id.titleBox)
    beatsPerMinuteBox = findViewById(R.id.beatsPerMinuteBox)
    notesBox = findViewById(R.id.notesBox)

    titleBox.addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(p0: Editable?) {}
      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
      override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        song.title = titleBox.text.toString()
      }
    })

    notesBox.addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(p0: Editable?) {}
      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
      override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        song.notes = notesBox.text.toString()
      }
    })

    beatsPerMinuteBox.addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(p0: Editable?) {}
      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
      override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        try {
          song.beatsPerMinute = beatsPerMinuteBox.text.toString().toInt()
        } catch (e: NumberFormatException) {
        }
      }
    })

    syncUI()
  }

  private fun syncUI() {
    titleBox.setText(song.title)
    beatsPerMinuteBox.setText(song.beatsPerMinute.toString())
    notesBox.setText(song.notes)
  }

  private fun play() {
    releasePlayer()

    val file = File.createTempFile("rtttl_", ".rtttl", cacheDir)
    song.write(file)

    player = MediaPlayer.create(this, Uri.fromFile(file)).apply {
      start()
      setOnCompletionListener {
        releasePlayer()
        file.delete()
      }
    }
  }

  private fun releasePlayer() {
    player?.stop()
    player?.release()
    player = null
  }

  override fun onStop() {
    super.onStop()
    prefs.edit().apply {
      putString("title", song.title)
      putInt("beatsPerMinute", song.beatsPerMinute)
      putString("notes", song.notes)
      apply()
    }
    releasePlayer()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.actionbar, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.playButton -> {
      play()
      true
    }
    R.id.setRingtoneButton -> {
      setRingtoneMaybe()
      true
    }
    else -> {
      super.onOptionsItemSelected(item)
    }
  }

  private fun setRingtoneMaybe() {
    val permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_SETTINGS)
    requestPermissions(permissions, 100, {
      setRingtone()
    }, {
      Toast.makeText(this, "Unable to set ringtone.", Toast.LENGTH_LONG).show()
    })
  }

  private fun setRingtone() {
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES), "rattler.rtttl")
    song.write(file)

    MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), null) { _, uri ->
      RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, uri)
      runOnUiThread {
        Toast.makeText(this, "Ringtone is set.", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
