package food_delivery_system.repository;

import food_delivery_system.model.Settings;
import food_delivery_system.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Single-row settings file. Format: restaurantCommissionPct|riderCommissionPct */
@Repository
public class SettingsRepository {
    private static final String FILE = "settings.txt";
    @Autowired private FileUtil fileUtil;

    public Settings load() {
        List<String> lines = fileUtil.readAllLines(FILE);
        for (String l : lines) {
            if (l == null || l.isBlank()) continue;
            String[] p = FileUtil.split(l);
            try {
                double rPct = p.length > 0 ? Double.parseDouble(p[0]) : 5.0;
                double riderPct = p.length > 1 ? Double.parseDouble(p[1]) : 10.0;
                return new Settings(rPct, riderPct);
            } catch (Exception ignored) {}
        }
        Settings s = new Settings();
        save(s);
        return s;
    }

    public void save(Settings s) {
        fileUtil.writeAllLines(FILE, java.util.List.of(
                FileUtil.join(s.getRestaurantCommissionPct(), s.getRiderCommissionPct())
        ));
    }
}
