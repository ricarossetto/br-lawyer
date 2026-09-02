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

async function captureTasksAndPalette() {
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

  async function clickNav(index) {
    const navButtons = await page.$$('aside nav button');
    if (navButtons[index]) {
      await navButtons[index].click();
      await sleep(2500);
    }
  }

  // 7. Tarefas / Kanban
  console.log('Navigating to Tarefas (Kanban)...');
  await clickNav(3); // Tarefas
  await sleep(3000);
  await saveScreenshot(page, '07_tasks_kanban.png');

  // 8. Open Task Drawer
  console.log('Opening Task Inspector Drawer...');
  const taskCards = await page.$$('div.cursor-grab, div.group');
  for (const card of taskCards) {
    const text = await page.evaluate(el => el.textContent, card);
    if (text && text.includes('Juntada')) {
      await card.click();
      await sleep(2500);
      await saveScreenshot(page, '08_task_inspector_drawer.png');
      break;
    }
  }

  // Close task drawer
  const closeBtns = await page.$$('aside button, button[title="Fechar"], button');
  for (const cb of closeBtns) {
    const title = await page.evaluate(el => el.getAttribute('title') || '', cb);
    if (title.includes('Fechar') || title.includes('close')) {
      await cb.click();
      break;
    }
  }
  await sleep(1000);

  // 9. Switch to List Mode
  console.log('Switching to Tasks List Mode...');
  const buttons = await page.$$('button');
  for (const b of buttons) {
    const title = await page.evaluate(el => el.getAttribute('title') || '', b);
    if (title.includes('Lista')) {
      await b.click();
      await sleep(2500);
      await saveScreenshot(page, '09_tasks_list.png');
      break;
    }
  }

  // 12. Command Palette (Ctrl+K)
  console.log('Triggering Command Palette (Ctrl+K)...');
  await clickNav(0); // Dashboard
  await sleep(1500);
  await page.keyboard.down('Control');
  await page.keyboard.press('KeyK');
  await page.keyboard.up('Control');
  await sleep(2000);
  await saveScreenshot(page, '12_command_palette.png');

  await browser.close();
  console.log('Tasks and Palette captures complete!');
}

captureTasksAndPalette().catch(err => {
  console.error('Error during capture:', err);
  process.exit(1);
});