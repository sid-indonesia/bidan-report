package org.sidindonesia.bidanreport.domain.pk;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ClientId implements Serializable {
	private long sourceId;
	private LocalDateTime dateCreated;
}
