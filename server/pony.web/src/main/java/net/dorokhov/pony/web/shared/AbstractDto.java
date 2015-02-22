package net.dorokhov.pony.web.shared;

public class AbstractDto<T> {

	private T id;

	public T getId() {
		return id;
	}

	public void setId(T aId) {
		id = aId;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object aObj) {

		if (this == aObj) {
			return true;
		}

		if (aObj != null && getId() != null && getClass().equals(aObj.getClass())) {

			AbstractDto entity = (AbstractDto) aObj;

			return getId().equals(entity.getId());
		}

		return false;
	}

}
