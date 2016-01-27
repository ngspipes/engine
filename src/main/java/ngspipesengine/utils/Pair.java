package ngspipesengine.utils;

public class Pair<T, R> {

	T key;
	public T getKey(){ return key;}
	
	R value;
	public R getValue() { return value; }
	
	public Pair(T key, R value) {
		this.key = key;
		this.value = value;
	}
}
