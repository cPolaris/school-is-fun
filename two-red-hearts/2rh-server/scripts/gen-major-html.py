MAJOR_FILE = '../raw-data/majors.txt'
OUT_FILE = 'output/majors.html'


def escape_html(unsafe):
    return unsafe.replace('&', '&amp;') \
        .replace('<', '&lt;') \
        .replace('>', '&gt;') \
        .replace('"', '&quot;') \
        .replace("'", '&#039;')


with open(OUT_FILE, 'w') as outf:
    outf.write('<md-input-container><label>Major</label><md-select name="major-selection" v-model="major">')

    with open(MAJOR_FILE, 'r') as f:
        lines = f.read().splitlines()
        major_id = 0
        for l in lines:
            outf.write('<md-option value="%s">%s</md-option>' % (major_id, escape_html(l)))
            major_id += 1

    outf.write('</md-select></md-input-container>')
