package ru.tsystems.tchallenge.contest.codemaster.managers.resources;

import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;

import java.nio.file.Path;

/**
 * Manager that simplify work with file system.
 * {@link ServiceResourceManager} hides real paths of files behinds name of business operations
 *
 * Glossary:
 * *  WorkDir - work directory - directory, which is bind to Docker container.
 *    So it may contain source and compiled file, input and output file
 * *  WorkDirName - name of work directory
 * *  DockerfileDir - directory, that contains Dockerfile for specific language and specific action
 *    (e.g. Dockerfile to compile .java file)
 * *  CodeFile - file, that contains source code
 * *  InputFile - file, that contains input for program
 * *  OutputFile - file, that contains output of program
 *
 * About workDir & workDirName:
 * WorkDir is absolute path to working directory.
 * Most of methods in {@link ServiceResourceManager}  accepts workDir as parameter
 * But it's a bad idea to store absolute path in database.
 * So instead of saving absolute path we will save only work directory name
 * {@link ServiceResourceManager} can restore absolute path of work directory by work directory name and language
 */
public interface ServiceResourceManager {
    /**
     * Retrieve path of dir, that contains Dockerfile for specified language and action type
     * @param language Code language, for which docker file is required (e.g. "JAVA")
     * @param dockerfileType Type of action (e.g. "COMPILATION")
     * @return path, that contains Dockerfile
     */
    Path getDockerfileDir(CodeLanguage language, DockerfileType dockerfileType);

    /**
     * Restore absolute path of working directory by it name and language
     * @param workDirName name of working directory
     * @param language code language
     * @return absolute path of working directory
     */
    Path getWorkDir(String workDirName, CodeLanguage language);

    /**
     * Create new work directory for specified language
     * @param language code language
     * @return Path for created work directory
     */
    Path createWorkDir(CodeLanguage language);

    /**
     * Create code file in specified work directory and write code to it.
     * @param workDir absolute path of working directory
     * @param sourceCode data, that will be written to the file
     * @return path for created file
     */
    Path createAndWriteCodeFile(Path workDir, String sourceCode);

    /**
     * Create input file in specified work directory and write input to it
     * @param workDir absolute path of working directory
     * @param input data, that will be written to the file
     * @return path for the created file
     */
    Path createAndWriteInputFile(Path workDir, String input);

    /**
     * Create output file in specified work (if not exists) directory and write input to it
     * @param workDir absolute path of working directory
     * @param output data, that will be written to the file
     * @return path for the created file
     */
    Path createAndWriteOutputFile(Path workDir, String output);

    /**
     * Read input file in specified working directory
     * @param workDir absolute path of working directory
     * @return input for program or null if file is missing
     */
    String readInput(Path workDir);

    /**
     * Read output file in specified working directory
     * @param workDir absolute path of working directory
     * @return output for program or null if file is missing
     */
    String readOutput(Path workDir);

    /**
     * Read source code of program in specified working directory
     * @param workDir absolute path of working directory
     * @return source code
     */
    String readSourceCode(Path workDir);

    /**
     * Copy files from first working directory to second working directory.
     * Both directories must exist
     * @param workDirFrom absolute path of working directory, from which need to copy files
     * @param workDirTo absolute path of working directory, to which need to paste files
     */
    void cloneWorkDir(Path workDirFrom, Path workDirTo);
}
