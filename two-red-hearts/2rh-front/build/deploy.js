// https://github.com/shelljs/shelljs
require('./check-versions')()

process.env.NODE_ENV = 'production';

const deployDirectoryRel = '../2rh.github.io';
const ora = require('ora');
const path = require('path');
const chalk = require('chalk');
const spawn = require('child_process').spawn;
const shell = require('shelljs')
const webpack = require('webpack');
const config = require('../config');
const webpackConfig = require('./webpack.prod.conf');

const spinner = ora('building for production...');
spinner.start()

const assetsPath = path.join(config.build.assetsRoot, config.build.assetsSubDirectory);
shell.rm('-rf', assetsPath)
shell.mkdir('-p', assetsPath)
shell.config.silent = true
shell.cp('-R', 'static/*', assetsPath)
shell.config.silent = false

webpack(webpackConfig, function (err, stats) {
  spinner.stop()
  if (err) throw err
  process.stdout.write(stats.toString({
      colors: true,
      modules: false,
      children: false,
      chunks: false,
      chunkModules: false
    }) + '\n\n')

  console.log(chalk.cyan('  Build complete.\n'))
  console.log(chalk.yellow(
    '  Tip: built files are meant to be served over an HTTP server.\n' +
    '  Opening index.html over file:// won\'t work.\n'
  ))

  // Copy dist files to deploy directory and then
  // push with git
  spinner.text = 'Deploying with git';

  const deployPath = path.resolve(deployDirectoryRel);
  const currentDate = new Date().toISOString();
  console.info(chalk.yellow(`\nTarget directory: ${deployPath}`));
  shell.cp('-R', 'dist/*', deployPath)

  shell.cd(deployPath);
  let git = spawn(`git add --all && git commit --amend -m "${currentDate}" && git push -f`, {'shell': true});

  git.stdout.on('data', data => {
    console.log(`git: ${data}`);
  });

  git.stderr.on('data', data => {
    console.log(`git: ${data}`);
  });

  git.on('close', code => {
    spinner.succeed();
  });
})
