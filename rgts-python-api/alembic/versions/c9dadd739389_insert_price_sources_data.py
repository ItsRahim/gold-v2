"""INSERT price_sources data

Revision ID: c9dadd739389
Revises: 43c6c0315585
Create Date: 2025-03-21 13:12:05.685149

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision: str = 'c9dadd739389'
down_revision: Union[str, None] = '43c6c0315585'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade():
    op.execute("""
    INSERT INTO price_sources (name, endpoint, url, element, is_active)
    VALUES 
    ('UK Investing', 'uk-investing', 'https://uk.investing.com/currencies/xau-gbp', '["div", {"data-test": "instrument-price-last"}]', TRUE),
    ('Bloomberg', 'bloomberg', 'https://www.bloomberg.com/quote/XAUGBP:CUR', '["div", {"data-component": "sized-price", "class": "sized-price SizedPrice_extraLarge-05pKbJRbUH8-"}]', FALSE),
    ('CNBC', 'cnbc', 'https://www.cnbc.com/quotes/XAUGBP=', '["span", {"class": "QuoteStrip-lastPrice"}]', FALSE),
    ('Forbes', 'forbes', 'https://www.forbes.com/advisor/money-transfer/currency-converter/xau-gbp/', '["span", {"class": "amount"}]', FALSE),
    ('Gold UK', 'gold-uk', 'https://www.cnbc.com/quotes/XAUGBP=', '["span", {"class": "QuoteStrip-lastPrice"}]', FALSE);
    """)


def downgrade():
    op.execute(
        """DELETE FROM price_sources WHERE name IN ('UK Investing', 'Bloomberg', 'CNBC', 'Forbes', 'Gold UK');""")
