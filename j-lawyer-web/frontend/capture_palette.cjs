const puppeteer = require('puppeteer-core');
const fs = require('fs');
const path = require('path');

const CHROME_PATH = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const ARTIFACT_DIR = 'C:\\Users\\Ricardo PC\\.gemini\\antigravity\\brain\\6e86071e-04a5-4476-bf13-974e601a24f4\\screenshots';
const DOCS_DIR = 'C:\\projetos IA\\BR-LAWYER\\br-lawyer-webui\\docs\\screenshots';

fs.mkdirSync(ARTIFACT_DIR, { recursive: true });
fs.mkdirSync(DOCS_DIR, { recursive: true });

async function sleep(ms) {
  return new Promise(r => setTimeout(r, ms));
}

async function saveScreenshot(page, filename) {
  const p1 = path.join(ARTIFACT_DIR, filename);
  const p2 = path.join(DOCS_DIR, filename);
  await page.screenshot({ path: p1 });
  fs.copyFileSync(p1, p2);
  console.log('✓ Saved screenshot:', filename);
}

async function capturePalette() {
  const browser = await puppeteer.launch({
    executablePath: CHROME_PATH,
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox', '--window-size=1440,900']
  });

  const page = await browser.newPage();
  await page.setViewport({ width: 1440, height: 900 });

  // 1. Login
  await page.goto('http://127.0.0.1:3000', { waitUntil: 'networkidle0' });
  await page.click('button[type="submit"]');
  await page.waitForSelector('aside nav button', { timeout: 15000 });
  await sleep(2500);

  // Click center command bar trigger
  console.log('Clicking center search button in topbar...');
  const searchBtn = await page.$('header button');
  if (searchBtn) {
    await searchBtn.click();
    await sleep(2000);
    await saveScreenshot(page, '12_command_palette.png');
  }

  await browser.close();
  console.log('Command palette captured!');
}

capturePalette().catch(err => {
  console.error('Error during capture:', err);
  process.exit(1);
});