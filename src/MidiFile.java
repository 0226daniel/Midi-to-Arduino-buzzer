import java.io.File;

/**
 * Created by kimjisub on 2016. 4. 13..
 */
public class MidiFile {

	File file;
	String name;
	String title;
	String ext;

	MidiFile(File file) {
		this.file = file;
		try {
			name = file.getName();
			title = name.substring(0, name.lastIndexOf("."));
			ext = name.substring(name.lastIndexOf(".") + 1);
		}catch (StringIndexOutOfBoundsException e){
			name = file.getName();
			title = "";
			ext = "";
		}
	}
}
