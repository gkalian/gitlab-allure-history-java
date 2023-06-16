import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime

INDEX_TEXT_START = '''<!DOCTYPE html>
<html>
<head>
    <title>Index of {folderPath}</title>
    <style>
        table {
            font-family: Arial, sans-serif;
            border-collapse: collapse;
        }
        th {
            cursor: pointer;
        }
        th, td {
            border: 1px solid #ddd;
            text-align: left;
            padding: 8px;
        }
        th:hover {
            background-color: #ddd;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        tr:hover {
            background-color: #ddd;
        }
    </style>
    
    <script>
        function sortTable(n) {
          var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
          table = document.getElementsByTagName("table")[0];
          switching = true;
          dir = "asc";
          while (switching) {
            switching = false;
            rows = table.getElementsByTagName("tr");
            for (i = 1; i < (rows.length - 1); i++) {
              shouldSwitch = false;
              // Skip the row if it contains "../"
              if (rows[i].getElementsByTagName("td")[0].innerHTML.includes("../")) {
                continue;
              }
              x = rows[i].getElementsByTagName("td")[n];
              y = rows[i + 1].getElementsByTagName("td")[n];
              if (dir == "asc") {
                if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                  shouldSwitch = true;
                  break;
                }
              } else if (dir == "desc") {
                if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                  shouldSwitch = true;
                  break;
                }
              }
            }
            if (shouldSwitch) {
              rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
              switching = true;
              switchcount++;
            } else {
              if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
              }
            }
          }
        }
</script>

</head>
<body>
    <h2>Index of {folderPath}</h2>
    <hr>
    <table>
        <thead>
            <tr>
                <th><a href="#" onclick="sortTable(0);">Name</a></th>
                <th><a href="#" onclick="sortTable(1);">Last modified</a></th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><a href='../'>../</a></td>
                <td></td>
            </tr>
'''
INDEX_TEXT_END = '''        </tbody>
    </table>
</body>
</html>
'''

def index_folder(path) {
    println("Indexing: ${path}/")
    // getting the content of the folder
    def files = new File(path).listFiles()

    // if Root folder, correcting folder name
    def root = path
    if (path.startsWith('public')) {
        root = path.replace('public', 'gitlab-allure-history')
    }

    def index_text = INDEX_TEXT_START.replace("{folderPath}", path.endsWith("public") ? path.replace("public", "allure-history") : path)

    files.each { file ->
        // Avoiding index.html files
        if (file.getName() != "index.html") {

            def last_modified_str = getLastModifiedDate(file)
            index_text += """\t\t<tr>\t\t\t<td><a href="${file.getName()}">${file.getName()}</a></td>\t\t\t<td>${last_modified_str}</td>\t\t</tr>"""

        }
    }
    index_text += INDEX_TEXT_END
    // create or override previous index.html
    // save indexed content to file
    new File(path + '/index.html').withWriter { writer ->
        writer.write(index_text)
    }
}

def folder_path = args[0]

// indexing root directory (script position)
index_folder(folder_path)

// calculate and convert timestamp from git for each folder
def getLastModifiedDate(file) {
    def last_commit_date = "git log -1 --format=%cd -- ${file.getPath()}".execute().text.trim()
    def date

    if (!last_commit_date || last_commit_date.isEmpty()) {
        date = LocalDateTime.now()
    } else {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy X", Locale.ENGLISH)
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(last_commit_date, formatter)
        date = zonedDateTime.toLocalDateTime()
    }

    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // select anytime zone if needed
    return outputFormatter.format(date.plusHours(3))
}
