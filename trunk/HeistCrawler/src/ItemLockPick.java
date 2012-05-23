
public class ItemLockPick extends Item {

	public ItemLockPick(String name) {
		super(1, name);
		
		// TODO Auto-generated constructor stub
	}
	
	public void unlockDoor(Door door) {
		if (door.isLocked()) {
			door.setLocked(false);
		}
	}
	
	
}
