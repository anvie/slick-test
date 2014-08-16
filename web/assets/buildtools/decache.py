#!/usr/bin/python

import os
import sys
import hashlib
import re
import shutil
import zlib
#from urlparse import urlparse

ASSETS_LINK_RE = re.compile(r"""<link.*?href="(.*?)".*?/?>""")
ASSETS_SCRIPT_RE = re.compile(r"""<script.*?src="(.*?)".*?>.*?</script>""")
MATCH_ALL_RE = re.compile(r"""<link.*?href="(.*?)".*?/?>|<script.*src="(.*?)">.*?</script>""")
REL_EXTRACTOR_RE = re.compile(r'rel="(.*?)"')
TYPE_EXTRACTOR_RE = re.compile(r'type="(.*?)"')
EXCLUDED_LINE_RE = re.compile(r"""""")
CSS_URL_RE = re.compile(r"""url\(['\"]?(.*?)['\"]?\)""")
EXCLUDED_PATH_RE = None

class colors:
	WARNING = '\033[93m'
	ENDC = '\033[0m'

def warn(text):
	print colors.WARNING + text + colors.ENDC

def md5sum(filename, blocksize=65536):
    hash = hashlib.md5()
    with open(filename, "r+b") as f:
        for block in iter(lambda: f.read(blocksize), ""):
            hash.update(block)
    return hash.hexdigest()

def crc32sum(filename):
	prev = 0
	f = open(filename, "rb")
	for eachLine in f:
		prev = zlib.crc32(eachLine, prev)
	f.close()
	return "%X" % (prev & 0xFFFFFFFF)

def clean_hash(link):
	rv = link
	if '?' in link:
		rv = link.split("?")[0]
	else:
		rv = link
	if '#' in rv:
		rv = rv.split("#")[0]
	else:
		rv = rv
	return rv

def get_abs_path(root, c_f_str, f_str):
	if f_str.startswith("http:") or f_str.startswith("https:"):
		return f_str

	if f_str.startswith("/"):
		return root + f_str[1:]

	dirname = os.path.dirname(c_f_str)
	#print "dirname:",os.path.dirname(c_f_str)
	#print "c_f_str",c_f_str
	#print "f_str",f_str
	#s = f_str.split("/")
	#p = []
	#for c in s:
	#	if c != ".." and c != ".":
	#		p.append(c)
	#return os.path.join(root, '/'.join(p))
	return os.path.abspath(dirname + "/" + f_str)


def file_exists(f):
	return os.path.isfile(f)


def process_file(root, f_str):
	global MATCH_ALL_RE, ASSETS_LINK_RE, ASSETS_SCRIPT_RE


	f = open(f_str, 'r')
	fo_str = f_str + ".decache.tmp"
	fo = open(fo_str, 'w')
	
	line_count = 0

	for line in f:
		line_count = line_count + 1

		if MATCH_ALL_RE.match(line.strip()):
			#print f_str + ": --> " + line.strip()

			# get link
			def replacer_css(_m):
				f_link = _m.group(1)

				if f_link.startswith("http"):
					return _m.group(0)

				f_link = clean_hash(f_link)

				rel = ""
				_type = ""
				_m2 = REL_EXTRACTOR_RE.search(_m.group(0))
				if _m2 is not None and len(_m2.groups()) > 0:
					rel = _m2.group(1)

				_m2 = TYPE_EXTRACTOR_RE.search(_m.group(0))
				if _m2 is not None and len(_m2.groups()) > 0:
					_type = " type=\"" + _m2.group(1) + "\""


				prefix = ""
				if f_link.startswith("/"):
					prefix = "/"
					f_link = f_link[1:]
				to = root + f_link
				if not os.path.isfile(to):
					warn("file not found: " + to + ", referenced from " + f_str + ":" + str(line_count))
					return """<link%s href="%s?not-found" rel="%s" />""".strip() % (_type, prefix + f_link, rel)

				h = crc32sum(to)

				rv = f_link
				if "?" in f_link:
					rv = prefix + f_link + "&h=" + h
				else:
					rv = prefix + f_link + "?h=" + h
				#print rv
				return """<link%s href="%s" rel="%s" />""".strip() % (_type, rv, rel)

			def replacer_script(_m):
				f_link = _m.group(1)
				
				if f_link.startswith("http"):
					return _m.group(0)

				f_link = clean_hash(f_link)

				prefix = ""
				if f_link.startswith("/"):
					prefix = "/"
					f_link = f_link[1:]
				to = root + f_link
				if not os.path.isfile(to):
					warn("file not found: " + to + ", referenced from " + f_str + ":" + str(line_count))
					return """<script type="text/javascript" src="%s?not-found"></script>""" % (prefix + f_link)

				h = crc32sum(to)
				
				rv = f_link
				if "?" in f_link:
					rv = prefix + f_link + "&h=" + h
				else:
					rv = prefix + f_link + "?h=" + h
				#print rv
				return """<script type="text/javascript" src="%s"></script>""" % rv




			m = ASSETS_LINK_RE.sub(replacer_css, line)
			m = ASSETS_SCRIPT_RE.sub(replacer_script, m)
			fo.write(m)
		
		elif f_str.endswith(".css") and CSS_URL_RE.search(line) is not None:

			#if 'icon-sprite' in line:
			#	print line

			def replacer_css_url(_m):
				#print f_str + ":" + _m.group(1)
				original = _m.group(0)

				t_link = _m.group(1)
				abs_path = clean_hash(get_abs_path(root, f_str, t_link))
				#print f_str,":",abs_path
				if not file_exists(abs_path):
				#	warn("file not found " + abs_path + ". referenced from " + f_str + ":" + str(line_count))
					return original

				h = crc32sum(abs_path)
				#print abs_path,":",h

				return 'url("' + clean_hash(t_link) + "?h=" + h + '")'

			repl = CSS_URL_RE.sub(replacer_css_url, line)

			fo.write(repl)



		else:
			fo.write(line)



	fo.close()
	f.close()

	# apply when changes occur
	if md5sum(fo_str) != md5sum(f_str):
		shutil.copy(fo_str, f_str)
	os.remove(fo_str)

def main():
	global EXCLUDED_PATH_RE

	argv = sys.argv[1:]
	opts = filter(lambda a: a.startswith("--"), argv)
	argv = filter(lambda a: not a.startswith("--"), argv)

	for opt in opts:
		key, value = opt.split("=")
		if key.startswith("--exclude"):
			EXCLUDED_PATH_RE = re.compile(value)

	tdir = argv[0]
	print "scanning " + tdir + "  ..."
	count = 0
	css_count = 0
	html_count = 0

	target_files = []

	for root, sub_dir, files in os.walk(tdir):

		for f in files:
			
			if EXCLUDED_PATH_RE is not None and EXCLUDED_PATH_RE.search(root) > 0:
				continue

			if not (f.endswith(".html") or f.endswith(".css")):
				continue

			tf = os.path.join(root, f)

			#print tf

			target_files.append(tf)


	# proses css terlebih dahulu
	for tf in target_files:
		if tf.endswith(".css"):
			process_file(tdir, tf)
			count = count + 1
			css_count = css_count + 1

	# proses html
	for tf in target_files:
		if tf.endswith(".html"):
			process_file(tdir, tf)
			count = count + 1
			html_count = html_count + 1


	print "done",count,"files proceeded (",css_count,"css, and",html_count,"html )."

def show_usage():
	print "usage: ./" + sys.argv[0] + " [options] [directory-to-scan]"
	print "options are:"
	print ""
	print "   --exclude=[regex-match-path]"

if __name__ == "__main__":
	if len(sys.argv) < 2:
		show_usage()
		sys.exit(2)
	print "Decache v0.1"
	main()






