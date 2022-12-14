fun main() {
    val root = readStructure(java.io.File("inputs/day7.txt").readLines())
    println(part1(root))
    println(part2(root))
}

private fun part1(root: Dir): Int {
    return root.allDirs()
        .filter { it.size() < 100000 }
        .sumOf { it.size() }
}

private fun part2(root: Dir): Int {
    val unusedSize = 70000000 - root.size()
    val sizeToDelete = 30000000 - unusedSize

    return root.allDirs()
        .map { it.size() }
        .filter { it >= sizeToDelete }
        .minOrNull()!!
}

private fun readStructure(output: List<String>): Dir {
    val root = Dir("/")
    var currentDir = root

    for (line in output) {
        when {
            line.startsWith("$ cd") ->
                currentDir = changeDir(line, currentDir, root)
            line.startsWith("$ ls") -> { // ignore
            }
            line.startsWith("dir") ->
                currentDir.add(Dir(line.substring(4), currentDir))
            else ->
                currentDir.addFileSize(line.split(" ")[0].toInt())
        }
    }
    return root
}

private fun changeDir(line: String, currentDir: Dir, root: Dir): Dir {
    return when (val to = line.substring(5)) {
        "/" -> root
        ".." -> currentDir.parent!!
        else -> currentDir.getDir(to) ?: Dir(to, currentDir).also {
            currentDir.add(it)
        }
    }
}

private data class Dir(val name: String, val parent: Dir? = null) {
    private var cachedSize: Int? = null
    private var totalFileSize: Int = 0
    private val subdirs: MutableList<Dir> = mutableListOf()

    fun getDir(name: String): Dir? =
        subdirs.find { it.name == name }

    fun add(dir: Dir) {
        subdirs.add(dir)
    }

    fun addFileSize(size: Int) {
        totalFileSize += size
    }

    fun allDirs(): List<Dir> =
        subdirs.flatMap { it.allDirs() } + this

    fun size(): Int {
        if (cachedSize == null) {
            cachedSize = totalFileSize + subdirs.fold(0) { size, it ->
                size + it.size()
            }
        }
        return cachedSize!!
    }
}