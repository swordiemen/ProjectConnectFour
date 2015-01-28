package test;

public class ClientTestUtil {
	private String lastInput;
	private boolean gotHello = false;;

	public ClientTestUtil() {
		setLastInput("");
	}

	public String getLastInput() {
		return lastInput;
	}

	public void setLastInput(String lastInput) {
		this.lastInput = lastInput;
	}

	public boolean isGotHello() {
		return gotHello;
	}

	public void setGotHello(boolean gotHello) {
		this.gotHello = gotHello;
	}
}
