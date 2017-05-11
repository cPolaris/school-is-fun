MAJOR_FILE = '../raw-data/hobbyTags.txt'
OUT_FILE = 'output/hobby.txt'


def escape_html(unsafe):
    return unsafe.replace('&', '&amp;') \
        .replace('<', '&lt;') \
        .replace('>', '&gt;') \
        .replace('"', '&quot;') \
        .replace("'", '&#039;')

# <md-checkbox v-model="checkbox">Regular Checkbox</md-checkbox>

with open(OUT_FILE, 'w') as outf:
    with open(MAJOR_FILE, 'r') as f:
        lines = f.read().splitlines()
        model_num = 0
        for l in lines:
            outf.write('<md-checkbox v-model="tagboxes[%s]">%s</md-checkbox>' % (model_num, l))
            model_num += 1
