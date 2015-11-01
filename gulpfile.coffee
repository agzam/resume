gulp = require 'gulp'
jade = require 'gulp-jade'
rename = require 'gulp-rename'
exec = require('child_process').exec

gulp.task 'convert', (done)->
    exec 'rm -f out.html && cat front-end-dev.org | pandoc -f markdown_github > out.html', (err)->
        unless err then done()

gulp.task 'md-to-html', [ 'convert' ], ->
    gulp.src 'index.jade'
    .pipe jade()
    .pipe rename 'index.html'
    .pipe gulp.dest '.'

gulp.task 'watch', -> gulp.watch ['./front-end-dev.org', './index.jade'], ['md-to-html']

gulp.task 'default', ['md-to-html']
