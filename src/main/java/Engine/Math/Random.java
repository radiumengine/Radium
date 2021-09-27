package Engine.Math;

public class Random {
	
	public static int RandomInt(int min, int max) {
		return (int) (Math.random() * max + min);
	}
	
	public static float RandomFloat(float min, float max) {
		return (float) (Math.random() * max + min);
	}
}
