### Adding a field to a configuration class

After having added a field in a configuration class:

* add a field entry in `project-generation.groovy` using an environment variable
* add a field entry in `BranchGenerationStep` and `ProjectGenerationStep` configuration file
* add the creation of the environment variable in `AbstractSeedStep`
