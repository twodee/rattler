package org.twodee.rattler

import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : PermittedActivity() {
  private lateinit var notesBox: EditText
  private lateinit var titleBox: EditText
  private lateinit var beatsPerMinuteBox: EditText
  private lateinit var songList: RecyclerView
  private lateinit var splitter: Guideline
  private lateinit var adapter: SongAdapter

  private var isEditorVisible: Boolean = false
  private var player: MediaPlayer? = null
  private var song = Song("", 100, "4c4 4c4 4g4 4g4 4a4 4a4 2g4")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    titleBox = findViewById(R.id.titleBox)
    beatsPerMinuteBox = findViewById(R.id.beatsPerMinuteBox)
    notesBox = findViewById(R.id.notesBox)
    songList = findViewById(R.id.songList)
    splitter = findViewById(R.id.splitter)

    titleBox.addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(p0: Editable?) {}
      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
      override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        song.title = titleBox.text.toString()
        adapter.update()
      }
    })

    notesBox.addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(p0: Editable?) {}
      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
      override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        song.notes = notesBox.text.toString()
        adapter.update()
      }
    })

    beatsPerMinuteBox.addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(p0: Editable?) {}
      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
      override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        try {
          song.beatsPerMinute = beatsPerMinuteBox.text.toString().toInt()
          adapter.update()
        } catch (e: NumberFormatException) {
        }
      }
    })

    adapter = SongAdapter(this)
    adapter.onSelect = {
      this.song = it
      syncUI()

      if (!isEditorVisible) {
        showEditor()
      }
    }
    adapter.onNothingSelected = {
      if (isEditorVisible) {
        hideEditor()
      }
    }

    songList.adapter = adapter
    songList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    songList.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))

    hideEditorImmediately()
    syncUI()
  }

  private fun hideEditorImmediately() {
    val params = splitter.layoutParams as ConstraintLayout.LayoutParams
    params.guidePercent = 1f
    splitter.layoutParams = params
    songList.invalidate()
  }

  private fun showEditor() {
    isEditorVisible = true
    animateEditor(1f, 0.3f)
  }

  fun hideEditor() {
    isEditorVisible = false
    animateEditor(0.3f, 1f)
  }

  private fun animateEditor(start: Float, end: Float) {
    ValueAnimator.ofFloat(start, end).apply {
      duration = 200
      interpolator = AccelerateDecelerateInterpolator()
      addUpdateListener {
        val params = splitter.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = it.animatedValue as Float
        splitter.layoutParams = params
      }
      start()
    }
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
    releasePlayer()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.actionbar, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.playButton -> {
      if (adapter.isSongSelected) {
        play()
      }
      true
    }
    R.id.setRingtoneButton -> {
      if (adapter.isSongSelected) {
        setRingtoneMaybe()
      }
      true
    }
    R.id.newSongButton -> {
      adapter.insert()
      true
    }
    R.id.deleteButton -> {
      adapter.delete()
      true
    }
    R.id.clearDatabaseButton -> {
      adapter.clear()
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
