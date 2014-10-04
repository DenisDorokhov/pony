package net.dorokhov.pony.core.domain;

import java.io.Serializable;
import java.util.Date;

public interface AbstractEntity<T extends Serializable> {

	public T getId();

	public void setId(T aId);

	public Date getCreationDate();

	public void setCreationDate(Date aCreationDate);

	public Date getUpdateDate();

	public void setUpdateDate(Date aUpdateDate);

}
