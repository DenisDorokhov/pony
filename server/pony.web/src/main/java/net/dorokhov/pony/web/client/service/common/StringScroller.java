package net.dorokhov.pony.web.client.service.common;

public class StringScroller {

	private final String target;

	private double offset;

	private double normalizedOffset;

	private int characterOffset;

	private String result;

	public StringScroller(String aTarget) {

		if (aTarget == null) {
			throw new NullPointerException();
		}

		target = aTarget;

		setOffset(0);
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double aOffset) {

		offset = aOffset;

		normalizedOffset = normalizeOffset(offset);

		updateResult();
	}

	public String getTarget() {
		return target;
	}

	public double getNormalizedOffset() {
		return normalizedOffset;
	}

	public int getCharacterOffset() {
		return characterOffset;
	}

	public String getResult() {
		return result;
	}

	private double normalizeOffset(double aOffset) {

		double normalizedOffset = aOffset;

		if (normalizedOffset > 0) { // 1.13 -> 0.13

			normalizedOffset = normalizedOffset - Math.floor(normalizedOffset);

		} else if (normalizedOffset < 0) { // -1.13 -> 0.87

			normalizedOffset = -offset;

			normalizedOffset = normalizedOffset - Math.floor(normalizedOffset);

			normalizedOffset = 1 - normalizedOffset;
		}

		return normalizedOffset;
	}

	private void updateResult() {

		characterOffset = (int)Math.round(getNormalizedOffset() * getTarget().length());

		String beginning = getTarget().substring(characterOffset);
		String ending = getTarget().substring(0, characterOffset);

		result = beginning + ending;
	}
}
