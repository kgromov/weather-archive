package events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncEvent implements Serializable {
    private static final long serialVersionUID = -2338626292552177485L;

    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;

    public static SyncEvent createDefault() {
        LocalDate now = LocalDate.now();
        return new SyncEvent(UUID.randomUUID(), now, now);
    }
}
