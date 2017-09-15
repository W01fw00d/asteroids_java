package classic;

public class Ship extends Mobile{

	// Used to determine the rate of firing
	protected int shotDelay, shotDelayLeft; 
	
	protected int[] xPts; 
	protected int[] yPts;
	
	protected double angle;
	
	protected int[] getXPts() {
		return xPts;
	}

	protected int[] getYPts() {
		return yPts;
	}
	
	protected int getShotDelay() {
		return shotDelay;
	}

	protected int getShotDelayLeft() {
		return shotDelayLeft;
	}

	protected double getAngle() {
		return angle;
	}

	protected void setShotDelayLeft(int shotDelayLeft) {
		this.shotDelayLeft = shotDelayLeft;
	}

	protected void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * Can the ship shoot again?
	 * @return
	 */
	protected boolean canShoot() {
		if (shotDelayLeft > 0){ 
			return false;
		}else{
			return true;
		}
	}	

	
}
