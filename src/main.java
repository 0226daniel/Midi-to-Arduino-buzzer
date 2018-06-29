/**
 * Created by kimjisub on 2016. 4. 13..
 */

import javax.sound.midi.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class main {

	private static final String[] NOTE_NAME = {"C", "CS", "D", "DS", "E", "F", "FS", "G", "GS", "A", "AS", "B"};

	private static Scanner scanner;

	private static ArrayList<MidiFile> fileList;


	public static void main(String[] args) throws Exception {

		scanner = new Scanner(System.in);


		while (true)
			mainLoop();
	}

	private static void mainLoop() throws InvalidMidiDataException, IOException {
		init();

		getFile();


		if (fileList.size() != 0) {

			MidiFile midiFile = selectFile();

			Sequence sequence = MidiSystem.getSequence(new File(System.getProperty("user.dir") + "/" + midiFile.name));

			Track track = selectTrack(midiFile, sequence);

			ArrayList<Note> noteList = new ArrayList<>();
			for (int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage shortMessage = (ShortMessage) message;

					int note = shortMessage.getData1();
					int octave = (note / 12) - 1;
					int tone = note % 12;
					int velocity = shortMessage.getData2();

					int command = shortMessage.getCommand();
					long tick = event.getTick();

					if (octave < 0 || 7 < octave) ;
					else if (command == 0x80 || velocity == 0)
						noteList.add(new Note(false, tick, note));
					else if (command == 0x90)
						noteList.add(new Note(true, tick, note));
				}
			}

			StringBuilder arduinoCode = new StringBuilder();
			FileReader basic = new FileReader("basic.txt");

			int c;
			while ((c = basic.read()) != -1) arduinoCode.append((char) c);
			basic.close();

			Note[] notes = new Note[noteList.size()];
			notes = noteList.toArray(notes);

			int piano[] = new int[200];
			for (int i = 0; i < piano.length; i++) piano[i] = -1;

			long prevTick = 0;
			for (int i = 0; i < noteList.size(); ) {
				int noteNum = 0;
				for (int j = 0; i + j < notes.length; j++) {
					if (notes[i].tick == notes[i + j].tick) noteNum++;
					else break;
				}

				int count = 0;
				for (int j = 0; j < 200; j++) {
					if (piano[j] != -1) {
						piano[j] += notes[i].tick - prevTick;
						count++;
					}
				}
				if (notes[i].pressed == true) {
					if (count == 0) {
						//println("delay " + (notes[i].tick - prevTick));
						arduinoCode.append("d(" + (notes[i].tick - prevTick) + ");\n");
					}
					piano[notes[i].keyNum] = 0;
				} else if (piano[notes[i].keyNum] > 0) {
					String stdd = "";
					for (int j = 0; j < noteNum && j < 5; j++) {
						int keyNum = notes[i + j].keyNum;
						int octave = (keyNum / 12) - 1;
						int note = keyNum % 12;
						stdd += "NOTE_" + NOTE_NAME[note] + octave + ", ";
					}
					//println(stdd + piano[notes[i].keyNum]);
					arduinoCode.append("p(" + stdd + piano[notes[i].keyNum] + ");\n");
					piano[notes[i].keyNum] = -1;
				}
				prevTick = notes[i].tick;
				i += noteNum;
			}
			arduinoCode.append("}");
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(arduinoCode.toString()), null);

			println("\n분석 결과가 클립보드에 복사되었습니다." + "\nspeed 변수의 값을 적절하게 조절하세요.");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			println();
			println();
			println();
			println();
			println();
			println();
			println();
			println();
			println();


		} else
			println("파일이 없습니다.");
	}

	private static void init() {
		fileList = new ArrayList<>();

		println("Midi to Arduino Buzzer (by kimjisub)\n");
	}

	private static void getFile() {
		File[] listFiles = (new File(System.getProperty("user.dir"))).listFiles();
		for (File file : listFiles) {
			MidiFile f = new MidiFile(file);

			if (file.isFile() && f.ext.equals("mid"))
				fileList.add(f);
		}
	}

	private static MidiFile selectFile() {

		for (int i = 0; i < fileList.size(); i++) {
			MidiFile file = fileList.get(i);
			println(i + 1 + ". " + file.title);
		}
		println();


		MidiFile file;

		while (true) {
			print("\n분석할 파일을 선택하세요 : ");
			try {
				file = fileList.get(scanner.nextInt() - 1);
				break;
			} catch (IndexOutOfBoundsException e) {
				println("잘못 선택하였습니다.");
			}
		}

		return file;
	}

	private static Track selectTrack(MidiFile midiFile, Sequence sequence) {
		println("\n==== " + midiFile.name + " ====");


		int trackCount = 0;
		for (Track track : sequence.getTracks()) {
			if (trackCount != 0)
				println("#" + trackCount + " : 노트 수 = " + track.size());
			trackCount++;
		}


		Track track;
		while (true) {
			print("\n분석할 트랙을 선택하세요 : ");
			try {
				track = sequence.getTracks()[scanner.nextInt()];
				break;
			} catch (IndexOutOfBoundsException e) {
				println("잘못 선택하였습니다.");
			}
		}

		return track;
	}


	private static void print(String str) {
		System.out.print(str);
	}

	private static void println(String str) {
		System.out.println(str);
	}

	private static void println() {
		System.out.println();
	}
}