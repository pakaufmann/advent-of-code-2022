fun main() {
    val root = readStructure(java.io.File("inputs/day7.txt").readLines())
    println(part1(root))
    println(part2(root))
}

private fun part1(root: Dir): Int {
    return root.allDirs().filter { it.size() < 100000 }.sumOf { it.size() }
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
            line.startsWith("$ cd") -> {
                currentDir = changeDir(line, currentDir, root)
            }
            line.startsWith("$ ls") -> {
                // ignore
            }
            line.startsWith("dir") -> {
                currentDir.add(Dir(line.substring(4), currentDir))
            }
            else -> {
                val (size, name) = line.split(" ")
                currentDir.add(File(name, size.toInt()))
            }
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

private sealed interface Node {
    val name: String
}

private data class Dir(
    override val name: String,
    val parent: Dir? = null,
    val content: MutableList<Node> = mutableListOf()
) : Node {
    private var cachedSize: Int? = null

    fun getDir(name: String): Dir? =
        content.find { it.name == name && it is Dir } as Dir?

    fun add(node: Node) {
        content.add(node)
    }

    fun allDirs(): List<Dir> =
        content
            .filterIsInstance(Dir::class.java)
            .flatMap { it.allDirs() } + this

    fun size(): Int {
        if (cachedSize == null) {
            cachedSize = content.fold(0) { size, it ->
                size + when (it) {
                    is Dir -> it.size()
                    is File -> it.size
                }
            }
        }
        return cachedSize!!
    }
}

private data class File(override val name: String, val size: Int) : Node {

}