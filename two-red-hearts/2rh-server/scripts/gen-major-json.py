MAJOR_FILE = '../raw-data/majors.txt'
OUT_FILE = 'output/majors.json'


def escape_html(unsafe):
    return unsafe.replace('&', '&amp;') \
        .replace('<', '&lt;') \
        .replace('>', '&gt;') \
        .replace('"', '&quot;') \
        .replace("'", '&#039;')


with open(OUT_FILE, 'w') as outf:
    outf.write('{')
    with open(MAJOR_FILE, 'r') as f:
        lines = f.read().splitlines()
        major_id = 0
        num_lines = len(lines)
        for i in range(num_lines):
            outf.write('"%s": "%s"' % (major_id, escape_html(lines[i])))
            if i <= num_lines - 2:
                outf.write(',')
            major_id += 1

    outf.write('}')
