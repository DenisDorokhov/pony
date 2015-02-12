package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseToken;

import javax.persistence.*;

@Entity
@Table(name = "access_token")
public class AccessToken extends BaseToken {

}
