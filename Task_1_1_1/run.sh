#!/bin/bash
set -e
rm -rf out doc app.jar

# 1. javac - компилятор.
# 2. -d SOMETHING - указывает, куда будут скомпилированы файлы
# с копированием пути - out/main/java/ru/nsu/oop
# 3. потом .java файл
javac -d out src/main/java/ru/nsu/oop/Main.java

# 1. javadoc - штука для документации.
# 2. -d SOMETHING - указывает, куда будут скомпилированы файлы
# 3. -sourcepath SOMETHING - указывает, где начинаются исходники
# 4. -subpackages SOMETHING - просто пакеты
javadoc -d doc -sourcepath src/main/java -subpackages ru.nsu.oop

# 1. jar - упаковщик.
# 2. --create - создаем файлик
# 3. --file SOMETHING - название файла
# 4. --main-class ru.nsu.oop.Main - какой вообще метод нужно запускать
# 5. -С out . - упаковываем все скомпилированное
jar --create --file app.jar --main-class ru.nsu.oop.Main -C out .

# джава, запускай app.jar
java -jar app.jar

