/**
 * Created by kimjisub on 2016. 4. 13..
 */
class Note {
	boolean pressed;
	long tick;
	int keyNum;

	public Note(boolean pressed, long tick, int keyNum){
		this.pressed = pressed;
		this.tick = tick;
		this.keyNum = keyNum;
	}
}