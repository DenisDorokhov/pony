package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseToken;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "refresh_token")
public class RefreshToken extends BaseToken {

}
