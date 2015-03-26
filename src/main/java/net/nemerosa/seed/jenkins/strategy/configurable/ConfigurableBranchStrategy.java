package net.nemerosa.seed.jenkins.strategy.configurable;

import net.nemerosa.seed.jenkins.model.ConfigurableBranchStrategyConfiguration;
import net.nemerosa.seed.jenkins.strategy.seed.SeedBranchStrategy;

public class ConfigurableBranchStrategy extends SeedBranchStrategy {

    private final ConfigurableBranchStrategyConfiguration configuration;

    public ConfigurableBranchStrategy(ConfigurableBranchStrategyConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getId() {
        return configuration.getId();
    }

//    @Override
//    protected String defaultSeed(String id) {
//        return evaluate()
//    }

    //    @Override
//    protected String defaultSeed(String id) {
//        return format("%1$s/%1$s_GENERATOR", defaultName(id).toUpperCase());
//    }
//
//    @Override
//    protected String defaultBranchSeed(String id) {
//        return format("%1$s/%1$s_*/%1$s_*_GENERATOR", defaultName(id).toUpperCase());
//    }
//
//    @Override
//    protected String defaultBranchStart(String id) {
//        return format("%1$s/%1$s_*/%1$s_*_010_BUILD", defaultName(id));
//    }
//
//    @Override
//    protected String getBranchName(String branch) {
//        String name;
//        if (StringUtils.startsWith(branch, "branches/")) {
//            name = StringUtils.substringAfter(branch, "/");
//        } else {
//            name = branch;
//        }
//        return super.getBranchName(name);
//    }
//
//    @Override
//    protected String getCommitParameter(SeedConfiguration configuration, SeedProjectConfiguration projectConfiguration) {
//        return "REVISION";
//    }
}
