package net.nemerosa.seed.triggering;

import net.nemerosa.seed.triggering.SeedChannel;

import java.util.Map;

public interface SeedLauncher {

    void launch(SeedChannel channel, String path, Map<String, String> parameters);

    /**
     * Deletes the item (folder or job) specified by the given path.
     */
    void delete(String path);
}