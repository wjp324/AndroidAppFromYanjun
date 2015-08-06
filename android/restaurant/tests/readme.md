# WordPress Android - Test Project #

## Run tests ##

    $ ant debug && ant installd && ant test

## Dump a test database ##

    $ adb shell su -c "echo .dump | sqlite3 /data/data/com.futcore.restaurant/databases/wordpress"
