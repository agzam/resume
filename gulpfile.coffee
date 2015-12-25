gulp = require 'gulp'
$ = require('gulp-load-plugins')()
jade = require 'gulp-jade'
rename = require 'gulp-rename'
exec = require('child_process').exec

gulp.task 'convert', (done)->
    exec 'rm -f out.html index.html && cat front-end-dev.org | pandoc -f markdown_github > out.html', (err)->
        unless err then done()

gulp.task 'md-to-html', [ 'convert' ], ->
    gulp.src 'index.jade'
    .pipe jade()
    .pipe rename 'index.html'
    .pipe gulp.dest '.'

gulp.task 'css', ->
    $.autoprefixer = require 'autoprefixer'

    gulp.src(['./source.css'])
    .pipe $.sourcemaps.init()
    .pipe $.postcss [
        require('postcss-nested')
        $.autoprefixer { browsers: ['last 2 version'] }
     ]
    .pipe($.concat 'own.css')
    .pipe $.sourcemaps.write()
    .pipe(gulp.dest './')

gulp.task 'watch', -> gulp.watch ['./front-end-dev.org', './index.jade', './source.css'], ['md-to-html', 'css']

gulp.task 'default', ['md-to-html', 'css']
