# Giltab Allure History Java

Example project using GitLab CI/CD for Allure report with history on GitLab Pages based **on Java**. This project exists only because of [Aleksandr's Kotlyar work](https://github.com/aleksandr-kotlyar/gitlab-allure-history/tree/master).
I needed to use Java instead of Python, so I made myself some changes:

- project is Maven now;
- original generate_index file is on groovy;
- additional html and css style in order to have "Last modified" column in results.

### How to use

- Enable **Pages** in your project
- In your main branch:
  - Extend your `gitlab-ci.yml` with stage "report" from this repo
  - Use image for this stage based on this Dockerfile
  - Copy `generate_index.groovy` script into your main repo folder
- Create branch `gl-pages`, it will be used for storing allure history
    - You can clean the whole branch from any file
    - Clear current `gitlab-ci.yml`
    - Create in the branch stage to publish Pages on Gitlab

Final results should be published on Gitlab Pages for each branch individually. 

### How it works

1. Job `test` is running tests on your current branch and saves allure-results and JOB_ID to artifacts for the next job in pipeline.
2. Job `allure`:
  - Clones `gl-pages` branch with it's all content into container (saving all previous reports).
  - Gets the 'history' of the last build from the same branch (if exists) into 'allure-results' of current build.
  - Creates 'executor.json' in 'allure-results' with build info and buildUrls in trends.
  - Generates report with allure Commandline into job_number build folder.
  - Creates branch-dir in 'gl-pages' `/public` directory if it's not existed yet.
  - Copies report into 'gl-pages' `/public/branch` directory: `/public/branch/job_number`.
  - Generates the index files for page tree `/public` and `/public/branch`.
  - Commits and pushes the public directory into 'gl-pages' branch into the repo.
3. And then push to branch 'gl-pages' triggers it's own job `pages` which publishes all content from `/public` directory on GitLab Pages. 
   
All data can be accessed from the root link of GitLab Pages, where you can always see all the history of each branch and find the latest execution by the latest job_number inside the branch-dir of the branch you are interested in.