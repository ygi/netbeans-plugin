Custom build
============

0. Because of plugin certification you must make your own build without the certificate. To allow that remove lines 'keystore=nbproject/private/keystore' and 'nbm_alias=myself' from the 'nbproject/project.properties' file.

1. download NetBeans IDE with JavaSE and PHP module
2. clone git repository - http://github.com/nette/netbeans-plugin
3. run NetBeans IDE and open cloned repository as a project
4. right click on the project name in the 'Project window'
5. left click on 'Create NBM'
6. when building is complete a '*.nbm' file is generated in the './build' directory
7. done :-) 
