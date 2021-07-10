package org.home.util.alerts

import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl

object Sound {
    fun playSound(times: Int, soundFilePath: String, decibels: Float? = null) {
        println("playing $soundFilePath")
        val clip: Clip = AudioSystem.getClip()
        clip.open(AudioSystem.getAudioInputStream(File(soundFilePath).absoluteFile))
        decibels?.let { (clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl).value = it }
        clip.loop(times)
        clip.start()
    }
}