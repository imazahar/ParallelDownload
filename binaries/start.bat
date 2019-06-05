start java -jar sb.jar "D:\Software Technology-Readings\Projects\Binaries\FileDownload\binaries"
SLEEP 1
start java -jar fs.jar "D:\Software Technology-Readings\Projects\Binaries\FileDownload\binaries"
SLEEP 5
start java -jar client.jar "D:\Software Technology-Readings\Projects\Binaries\FileDownload\binaries" "test.txt"
SLEEP 1